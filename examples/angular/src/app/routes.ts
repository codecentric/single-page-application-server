import {Routes} from '@angular/router';
import {DefaultPageComponent} from './default-page/default-page.component';
import {SecondPageComponent} from './second-page/second-page.component';


export const routes: Routes = [
  {
    path: 'second-page',
    component: SecondPageComponent,
  },
  {
    path: '',
    pathMatch: 'prefix',
    component: DefaultPageComponent,
  },
];
