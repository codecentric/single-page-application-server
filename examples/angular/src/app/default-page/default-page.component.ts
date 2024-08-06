import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-default-page',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './default-page.component.html'
})
export class DefaultPageComponent {
}
