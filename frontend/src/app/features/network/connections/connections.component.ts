import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { NetworkService } from '../../../core/services/network.service';
import { ApiResponse, Connection, User } from '../../../shared/models/models';

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
  errorMessage = '';
  successMessage = '';

  constructor(
    private networkService: NetworkService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {

    this.currentUserId = this.authService.getCurrentUserId() ?? 0;

    // load connections
    this.networkService.getConnections()
      .subscribe({
        next: r => {
          this.connections = r.data || [];
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to load connections.');
        }
      });

    // received requests
    this.networkService.getPendingRequests()
      .subscribe({
        next: r => {
          this.pendingRequests = r.data || [];
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to load pending requests.');
        }
      });

    // sent requests
    this.networkService.getSentRequests()
      .subscribe({
        next: r => {
          this.sentRequests = r.data || [];

          this.sentRequests.forEach(req => {
            this.sentUserIds.add(req.addresseeId);
          });
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to load sent requests.');
        }
      });

    // suggested users
    this.networkService.getSuggestedConnections(50)
      .subscribe({
        next: (users: User[]) => {
          this.allUsers = users || [];
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to load suggestions.');
        }
      });
  }

  sendRequest(user: User): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.isSelf(user.id)) {
      this.errorMessage = 'You cannot connect with yourself.';
      return;
    }

    if (this.isConnected(user.id) || this.sentUserIds.has(user.id)) {
      this.errorMessage = 'Connection already exists or request already sent.';
      return;
    }

    this.networkService.sendRequest(user.id)
      .subscribe({
        next: () => {
          this.sentUserIds.add(user.id);
          this.successMessage = `Connection request sent to @${user.username}.`;
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to send connection request.');
        }
      });
  }

  accept(c: Connection): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.networkService.acceptRequest(c.id)
      .subscribe({
        next: () => {

          this.pendingRequests =
            this.pendingRequests.filter(r => r.id !== c.id);

          this.connections.push({
            ...c,
            status: 'ACCEPTED'
          });

          this.successMessage = 'Connection request accepted.';
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to accept request.');
        }
      });
  }

  reject(c: Connection): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.networkService.rejectRequest(c.id)
      .subscribe({
        next: () => {

          this.pendingRequests =
            this.pendingRequests.filter(r => r.id !== c.id);

          this.successMessage = 'Connection request rejected.';
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to reject request.');
        }
      });
  }

  removeConnection(c: Connection): void {
    this.errorMessage = '';
    this.successMessage = '';

    const otherUserId = this.getOtherUserId(c);

    this.networkService.removeConnection(otherUserId)
      .subscribe({
        next: () => {

          this.connections =
            this.connections.filter(conn => conn.id !== c.id);

          this.sentUserIds.delete(otherUserId);
          this.successMessage = 'Connection removed.';
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to remove connection.');
        }
      });
  }

  removeConnectionFromDiscover(userId: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.networkService.removeConnection(userId)
      .subscribe({
        next: () => {

          this.connections =
            this.connections.filter(c =>
              c.requesterId !== userId && c.addresseeId !== userId
            );

          this.sentUserIds.delete(userId);
          this.successMessage = 'Connection removed.';
        },
        error: e => {
          this.errorMessage = this.extractErrorMessage(e, 'Unable to remove connection.');
        }
      });
  }

  isConnected(userId: number): boolean {

    return this.connections.some(c =>
      c.requesterId === userId || c.addresseeId === userId
    );
  }

  isSelf(userId: number): boolean {
    return this.currentUserId > 0 && this.currentUserId === userId;
  }

  getOtherUserName(c: Connection): string {

    return c.requesterId === this.currentUserId
      ? (c.addresseeFullName || c.addresseeUsername)
      : (c.requesterFullName || c.requesterUsername);
  }

  getOtherUserUsername(c: Connection): string {

    return c.requesterId === this.currentUserId
      ? c.addresseeUsername
      : c.requesterUsername;
  }

  getOtherUserId(c: Connection): number {

    return c.requesterId === this.currentUserId
      ? c.addresseeId
      : c.requesterId;
  }

  private extractErrorMessage(error: unknown, fallback: string): string {
    const httpError = error as HttpErrorResponse;
    const payload = httpError?.error as ApiResponse<unknown> | undefined;
    return payload?.message || fallback;
  }
}
