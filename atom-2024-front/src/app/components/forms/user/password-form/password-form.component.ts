import {Component} from '@angular/core';
import {InputTextModule} from "primeng/inputtext";
import {NgIf} from "@angular/common";
import {PaginatorModule} from "primeng/paginator";
import {PasswordModule} from "primeng/password";
import {ControlContainer, NgForm} from "@angular/forms";

@Component({
  selector: 'app-password-form',
  standalone: true,
    imports: [
        InputTextModule,
        NgIf,
        PaginatorModule,
        PasswordModule
    ],
  templateUrl: './password-form.component.html',
  styleUrl: './password-form.component.css',
  viewProviders: [ { provide: ControlContainer, useExisting: NgForm } ]
})
export class PasswordFormComponent {
  public password: string;
  public confirmationPassword: string;
}
