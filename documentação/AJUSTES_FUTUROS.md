# 🔧 Ajustes e Melhorias Futuras

## Sumário
Este documento descreve possíveis ajustes, otimizações e melhorias que podem ser feitos na API de Movimentações.

---

## 1️⃣ Suporte a Receitas

### Situação Atual
A API atualmente considera apenas **despesas** (todas as movimentações são negativas no saldo).

### Como Adicionar Receitas

**Option A: Campo de Tipo na Entidade Gasto**
```java
// Adicionar à classe Gasto
@Column(nullable = false)
@Enumerated(EnumType.STRING)
private TipoMovimentacao tipo; // RECEITA, DESPESA

// Criar enum
public enum TipoMovimentacao {
    RECEITA,
    DESPESA
}
```

**Option B: Nova Entidade Separada**
```java
@Entity
@Table(name = "receitas")
public class Receita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String descricao;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valor;
    
    @NotBlank
    private String categoria;
    
    @Column(name = "data_receita")
    private LocalDateTime dataReceita;
    
    // getters/setters
}
```

### Ajustes Necessários em MovimentacaoService

```java
// Método obterResumoFinanceiro - versão melhorada
@Transactional(readOnly = true)
public ResumoFinanceiroDTO obterResumoFinanceiro(int dias) {
    LocalDateTime inicio = LocalDateTime.now().minusDays(dias);
    LocalDateTime fim = LocalDateTime.now();

    List<Gasto> gastos = gastoRepository.findByDataGastoBetween(inicio, fim);
    
    BigDecimal totalReceitas = gastos.stream()
        .filter(g -> g.getTipo() == TipoMovimentacao.RECEITA)
        .map(Gasto::getValor)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    BigDecimal totalDespesas = gastos.stream()
        .filter(g -> g.getTipo() == TipoMovimentacao.DESPESA)
        .map(Gasto::getValor)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    BigDecimal saldoAtual = totalReceitas.subtract(totalDespesas);

    return ResumoFinanceiroDTO.builder()
            .totalReceitas(totalReceitas)
            .totalDespesas(totalDespesas)
            .saldoAtual(saldoAtual)
            .build();
}
```

---

## 2️⃣ Filtros Avançados

### Adicionar Busca por Período

```java
// Novo endpoint no controller
@GetMapping("/periodo")
public ResponseEntity<?> obterMovimentacoesPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    // Implementação
    Pageable pageable = PageRequest.of(page, size);
    // Buscar gastos entre data início e fim
}
```

### Filtrar por Categoria

```java
@GetMapping("/categoria/{categoria}")
public ResponseEntity<?> obterMovimentacoesPorCategoria(
        @PathVariable String categoria,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    // Usar findByCategoria do repository
}
```

### Busca por Texto

```java
@GetMapping("/buscar")
public ResponseEntity<?> buscarMovimentacoes(
        @RequestParam String termo,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    // Usar findByPalavraChave do repository
}
```

---

## 3️⃣ Paginação em Relatórios

### Evolução Financeira com Paginação

```java
// Ao invés de retornar lista simples
public Page<EvolucaoFinanceiraDTO> obterEvolucaoFinanceira(
        int dias,
        int page,
        int size) {
    
    List<EvolucaoFinanceiraDTO> evolucao = calcularEvolucao(dias);
    Pageable pageable = PageRequest.of(page, size);
    
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), evolucao.size());
    
    return new PageImpl<>(
        evolucao.subList(start, end),
        pageable,
        evolucao.size()
    );
}
```

---

## 4️⃣ Cache Distribuído (Redis)

### Adicionar Dependência
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Habilitar Cache
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.create(factory);
    }
}
```

### Usar em Métodos
```java
@Cacheable(value = "resumoFinanceiro", key = "#dias")
public ResumoFinanceiroDTO obterResumoFinanceiro(int dias) {
    // Implementação
}

@CacheEvict(value = "resumoFinanceiro", allEntries = true)
public MovimentacaoDTO criarMovimentacao(MovimentacaoDTO dto) {
    // Implementação
}
```

---

## 5️⃣ Documentação Swagger/OpenAPI

### Adicionar Dependência
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```

### Anotar Controller
```java
@RestController
@RequestMapping("/api/movimentacoes")
@Tag(name = "Movimentações", description = "API para gerenciamento de movimentações financeiras")
public class MovimentacaoController {
    
    @GetMapping
    @Operation(summary = "Listar movimentações", description = "Retorna lista paginada de movimentações")
    @ApiResponse(responseCode = "200", description = "Lista de movimentações retornada com sucesso")
    public ResponseEntity<MovimentacaoResponseDTO> listarMovimentacoes(...) {
        // Implementação
    }
}
```

**Acessar documentação em:** `http://localhost:8080/swagger-ui.html`

---

## 6️⃣ Testes Unitários

### Exemplo de Teste
```java
@SpringBootTest
public class MovimentacaoServiceTest {
    
    @MockBean
    private GastoRepository gastoRepository;
    
    @MockBean
    private ExportacaoService exportacaoService;
    
    @InjectMocks
    private MovimentacaoService service;
    
    @Test
    public void testListarMovimentacoes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Gasto> gastos = Arrays.asList(new Gasto());
        Page<Gasto> page = new PageImpl<>(gastos, pageable, 1);
        
        when(gastoRepository.findAll(pageable)).thenReturn(page);
        
        // Act
        MovimentacaoResponseDTO resultado = service.listarMovimentacoes(0, 10);
        
        // Assert
        assertEquals(1, resultado.getContent().size());
        verify(gastoRepository, times(1)).findAll(pageable);
    }
}
```

---

## 7️⃣ Validação Customizada

### Anotação Customizada
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValorMinimoValidator.class)
public @interface ValorMinimo {
    String message() default "Valor deve ser maior que {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value() default "0.01";
}
```

### Usar na DTO
```java
public class MovimentacaoDTO {
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 3, max = 255)
    private String descricao;
    
    @NotNull
    @ValorMinimo("0.01")
    private BigDecimal valor;
}
```

---

## 8️⃣ Auditoria

### Adicionar Auditoria aos Registros
```java
@Entity
@Table(name = "movimentacoes")
@EntityListeners(AuditingEntityListener.class)
public class Gasto {
    
    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @CreatedBy
    @Column(name = "criado_por")
    private String criadoPor;
    
    @LastModifiedBy
    @Column(name = "modificado_por")
    private String modificadoPor;
}
```

---

## 9️⃣ WebSockets para Atualizações em Tempo Real

### Adicionar Dependência
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### Configurar WebSocket
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-movimentacoes").setAllowedOrigins("*").withSockJS();
    }
}
```

### Notificar Clientes
```java
@Service
public class MovimentacaoWebSocketService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public void notificarNovaMovimentacao(MovimentacaoDTO dto) {
        messagingTemplate.convertAndSend("/topic/movimentacoes", dto);
    }
}
```

---

## 🔟 Compressão de Respostas

### Application.properties
```properties
server.compression.enabled=true
server.compression.min-response-size=1024
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain
```

---

## Checklist de Implementação

- [ ] Adicionar suporte a receitas
- [ ] Implementar filtros avançados
- [ ] Adicionar paginação a relatórios
- [ ] Configurar cache (Redis)
- [ ] Implementar Swagger/OpenAPI
- [ ] Criar testes unitários (80%+ cobertura)
- [ ] Adicionar validação customizada
- [ ] Implementar auditoria
- [ ] Configurar WebSockets
- [ ] Habilitar compressão de respostas
- [ ] Adicionar rate limiting
- [ ] Implementar circuit breaker
- [ ] Configurar logs estruturados
- [ ] Preparar para containerização (Docker)
- [ ] Setup de CI/CD

---

## 📚 Referências

- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/)
- [Spring Cache Abstraction](https://spring.io/guides/gs/caching/)
- [Spring WebSocket](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [Springdoc OpenAPI](https://springdoc.org/)

---

**Documento Criado:** 2025-11-14  
**Versão:** 1.0  
**Status:** Pronto para Implementação

