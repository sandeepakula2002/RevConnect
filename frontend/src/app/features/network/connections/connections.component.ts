
import { Component, OnInit } from '@angular/core';
import { NetworkService } from '../../../core/services/network.service';
import { Connection, User } from '../../../shared/models/models';

@Component({
  selector: 'app-connections',
  templateUrl: './connections.component.html',
  styleUrls: ['./connections.component.css']
})
export class ConnectionsComponent implements OnInit {

  tab = 'discover';

  connections: Connection[] = [];
  pendingRequests: Connection[] = [];
  sentRequests: Connection[] = [];
  allUsers: User[] = [];

  sentUserIds = new Set<number>();
  currentUserId = 0;

  constructor(private networkService: NetworkService) {}

  ngOnInit(): void {
    const stored = localStorage.getItem('currentUser');
    if (stored) {
      this.currentUserId = JSON.parse(stored).id;
    }

    this.networkService.getConnections()
      .subscribe(r => this.connections = r.data);

    this.networkService.getPendingRequests()
      .subscribe(r => this.pendingRequests = r.data);

    this.networkService.getSentRequests()
      .subscribe(r => {
        this.sentRequests = r.data;
        this.sentRequests.forEach(req =>
          this.sentUserIds.add(req.addressee.id)
        );
      });

    this.networkService.getSuggestedConnections(50)
      .subscribe((users: User[]) => {
        this.allUsers = users;
      });
  }

  sendRequest(user: User): void {
    this.networkService.sendRequest(user.id)
      .subscribe(() => {
        this.sentUserIds.add(user.id);
      });
  }

  getOtherUser(c: Connection): User {
    return c.requester.id === this.currentUserId
      ? c.addressee
      : c.requester;
  }

  accept(c: Connection): void {
    this.networkService.acceptRequest(c.id)
      .subscribe(() => {
        this.pendingRequests =
          this.pendingRequests.filter(r => r.id !== c.id);

        this.connections.push({ ...c, status: 'ACCEPTED' });
      });
  }

  reject(c: Connection): void {
    this.networkService.rejectRequest(c.id)
      .subscribe(() => {
        this.pendingRequests =
          this.pendingRequests.filter(r => r.id !== c.id);
      });
  }

  removeConnection(c: Connection): void {
    const otherUser = this.getOtherUser(c);

    this.networkService.removeConnection(otherUser.id)
      .subscribe(() => {
        this.connections =
          this.connections.filter(conn => conn.id !== c.id);
      });
  }
}