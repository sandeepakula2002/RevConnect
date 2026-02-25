import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConnectionsComponent } from './connections/connections.component';
import { RequestsComponent } from './requests/requests.component';

const routes: Routes = [
  { path: 'connections', component: ConnectionsComponent },
  { path: 'requests', component: RequestsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NetworkRoutingModule { }