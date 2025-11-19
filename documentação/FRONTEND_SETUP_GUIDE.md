# Guia de Configuração do Frontend - Autenticação JWT

## Problema
O backend retorna **HTTP 403 "Acesso não autorizado"** porque o frontend não está enviando o token JWT nas requisições.

## Solução
Use um **HttpInterceptor** para adicionar automaticamente o token JWT a todas as requisições HTTP.

---

## 📋 Implementação (Angular)

### 1. Criar o Token Interceptor

Crie o arquivo `src/app/interceptors/token.interceptor.ts`:

```typescript
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('auth_token');

    if (token) {
      // Clonar a requisição e adicionar o header Authorization
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
      return next.handle(cloned);
    }

    return next.handle(req);
  }
}
```

---

### 2. Registrar o Interceptor no AppModule

No seu `src/app/app.module.ts` (ou `app.config.ts` se estiver usando standalone):

#### Para módulos convencionais (AppModule):

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, HttpClientModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
```

#### Para aplicações standalone (Angular 14+):

No seu `main.ts`:

```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { TokenInterceptor } from './app/interceptors/token.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(
      withInterceptors([/* seus outros interceptors */])
    ),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
  ],
}).catch((err) => console.error(err));
```

---

### 3. Atualizar o Serviço de Login

Seu `gastos.service.ts` (ou `auth.service.ts`) deve salvar o token ao fazer login:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http
      .post<any>(`${this.apiUrl}/login`, { username, password })
      .pipe(
        tap((response) => {
          // Salvar o token no localStorage
          if (response.token) {
            localStorage.setItem('auth_token', response.token);
            console.log('Token salvo:', response.token);
          }
        })
      );
  }

  logout(): void {
    localStorage.removeItem('auth_token');
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
```

---

### 4. Atualizar o Serviço de Gastos

Seu `gastos.service.ts` pode agora fazer requisições normalmente:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GastosService {
  private apiUrl = 'http://localhost:8080/api/gastos';

  constructor(private http: HttpClient) {}

  listarGastos(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      catchError((error) => {
        console.error('Erro ao carregar gastos:', error);
        // Retornar array vazio em caso de erro
        return of([]);
      })
    );
  }

  buscarGasto(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  criarGasto(gasto: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, gasto);
  }

  atualizarGasto(id: number, gasto: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, gasto);
  }

  excluirGasto(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
```

---

## ✅ Testando a Solução

### 1. **Testar via Curl (backend está rodando)**

```bash
# 1. Fazer login e obter token
TOKEN=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq -r '.token')

echo "Token obtido: $TOKEN"

# 2. Usar o token para chamar /api/gastos
curl -i http://localhost:8080/api/gastos \
  -H "Authorization: Bearer $TOKEN"
```

Se retornar **HTTP 200** com uma lista JSON, o backend está funcionando ✅

### 2. **Testar no Frontend (Angular)**

No seu `gastos.component.ts`:

```typescript
import { Component, OnInit } from '@angular/core';
import { AuthService } from './services/auth.service';
import { GastosService } from './services/gastos.service';

@Component({
  selector: 'app-gastos',
  templateUrl: './gastos.component.html',
  styleUrls: ['./gastos.component.css'],
})
export class GastosComponent implements OnInit {
  gastos: any[] = [];
  loading = false;

  constructor(
    private authService: AuthService,
    private gastosService: GastosService
  ) {}

  ngOnInit() {
    // 1. Fazer login primeiro
    this.authService.login('admin', 'admin').subscribe(
      (response) => {
        console.log('Login realizado:', response);
        // 2. Depois carregar gastos (agora o interceptor adicionará o token)
        this.loadGastos();
      },
      (error) => {
        console.error('Erro ao fazer login:', error);
      }
    );
  }

  loadGastos() {
    this.loading = true;
    this.gastosService.listarGastos().subscribe(
      (data) => {
        this.gastos = data;
        console.log('Gastos carregados:', this.gastos);
        this.loading = false;
      },
      (error) => {
        console.error('Erro ao carregar gastos:', error);
        this.loading = false;
      }
    );
  }
}
```

---

## 🔐 Fluxo Completo

```
1. Usuário faz login
   ↓
2. Backend retorna token JWT (+ cookie)
   ↓
3. Frontend salva token em localStorage
   ↓
4. TokenInterceptor adiciona "Authorization: Bearer <token>" a TODAS as requisições
   ↓
5. Backend valida token via JwtAuthenticationFilter
   ↓
6. Requisição autenticada é permitida (HTTP 200)
```

---

## 🛠️ Alternativa: Usar Cookies (Menos Recomendado para Localhost)

Se preferir usar cookies em vez de localStorage:

```typescript
// No TokenInterceptor, adicionar credenciais
const cloned = req.clone({
  withCredentials: true, // Enviar cookies
  setHeaders: {
    'Content-Type': 'application/json',
  },
});
```

**⚠️ Aviso**: Cookies com `SameSite=None` exigem HTTPS em produção. Para localhost, pode não funcionar.
O método com localStorage + Authorization header é mais simples para desenvolvimento.

---

## 📝 Checklist Final

- [ ] Criar `token.interceptor.ts`
- [ ] Registrar `TokenInterceptor` no `AppModule` (ou `main.ts` para standalone)
- [ ] Atualizar `AuthService` para salvar token ao fazer login
- [ ] Testar login + requisição a `/api/gastos` no frontend
- [ ] Verificar no DevTools que o header `Authorization: Bearer <token>` está sendo enviado
- [ ] Se ainda houver erro, verificar se o token está sendo gerado corretamente no backend

---

## 🐛 Debugging

Se ainda tiver erro 403:

1. **Verificar se o token está sendo salvo**:
   ```javascript
   // No console do navegador
   localStorage.getItem('auth_token')
   ```

2. **Verificar se o header está sendo enviado**:
   - Abrir DevTools (F12)
   - Ir para "Network"
   - Fazer requisição a `/api/gastos`
   - Clicar na requisição e verificar se tem header `Authorization: Bearer ...`

3. **Verificar se o token é válido**:
   - Testar com curl (veja seção "Testando a Solução")
   - Se curl funciona mas navegador não, problema é no frontend (token/interceptor)
   - Se curl não funciona, problema é no backend (JWT expirado, secret inválido, etc.)

---

## Próximos Passos

Implemente os passos acima no seu projeto Angular e me relate:
1. Se o login funciona e o token é salvo
2. Se o header Authorization está sendo enviado (verificar DevTools)
3. Se recebe HTTP 200 ou continua com 403

Estou pronto para ajudar com mais ajustes! 🚀

