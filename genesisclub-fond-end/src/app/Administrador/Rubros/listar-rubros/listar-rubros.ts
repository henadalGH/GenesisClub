import { Component } from '@angular/core';
import { HeaderAdmin } from "../../header-admin/header-admin";
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-listar-rubros',
  imports: [HeaderAdmin, RouterLink],
  templateUrl: './listar-rubros.html',
  styleUrl: './listar-rubros.css',
})
export class ListarRubros {

}
