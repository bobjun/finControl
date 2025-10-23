import { Component, OnInit } from '@angular/core';
import { MovimentacoesService } from '../../services/movimentacoes.service';
import { ChartData, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  receitas = 0;
  despesas = 0;

  public pieChartData: ChartData<'pie', number[], string | string[]> = {
    labels: ['Receitas', 'Despesas'],
    datasets: [{ data: [0, 0] }]
  };
  public pieChartOptions: ChartOptions<'pie'> = { responsive: true };

  constructor(private movimentacoesService: MovimentacoesService) {}

  ngOnInit(): void {
    this.movimentacoesService.listar().subscribe(data => {
      this.receitas = data.filter(m => m.tipo === 'RECEITA').reduce((sum, m) => sum + m.valor, 0);
      this.despesas = data.filter(m => m.tipo === 'DESPESA').reduce((sum, m) => sum + m.valor, 0);
      this.pieChartData.datasets[0].data = [this.receitas, this.despesas];
    });
  }
}
