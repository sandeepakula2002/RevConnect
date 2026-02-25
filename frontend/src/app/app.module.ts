import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FeedComponent } from './feed/feed.component';
import { NotificationBellComponent } from './shared/components/notification-bell/notification-bell.component';
import { NotificationListComponent } from './shared/components/notification-list/notification-list.component';

@NgModule({
  declarations: [
    AppComponent,
    FeedComponent,
    NotificationBellComponent,
    NotificationListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
