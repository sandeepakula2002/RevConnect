import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NetworkRoutingModule } from './network-routing.module';
import { ConnectionsComponent } from './connections/connections.component';
import { RequestsComponent } from './requests/requests.component';


@NgModule({
  declarations: [
    ConnectionsComponent,
    RequestsComponent
  ],
  imports: [
    CommonModule,
    NetworkRoutingModule
  ]
})
export class NetworkModule { }
