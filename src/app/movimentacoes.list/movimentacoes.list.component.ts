import { Component, OnInit } from '@angular/core';
import { MovimentacoesService } from '../../services/movimentacoes.service';
import { Movimentacao } from '../../models/movimentacao.model';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  movimentacoes: Movimentacao[] = [];
  displayedColumns: string[] = ['descricao', 'valor', 'tipo', 'data'];

  constructor(private service: MovimentacoesService) {}

  ngOnInit(): void {
    this.service.listar().subscribe(data => this.movimentacoes = data);
  }
}
