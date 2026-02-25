import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Connection {
  id?: number;
  senderId: number;
  receiverId: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt?: Date;
}

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  
  // Direct URL to your Spring Boot backend
  // Change the port if your Spring Boot runs on a different port
  private apiUrl = 'http://localhost:8080/api/network';
  
  constructor(private http: HttpClient) {
    console.log('Network Service initialized with API URL:', this.apiUrl);
  }
  
  /**
   * Send connection request
   * Matches Spring Boot controller's @RequestParam parameters
   */
  sendRequest(senderId: number, receiverId: number): Observable<Connection> {
    const url = `${this.apiUrl}/request?senderId=${senderId}&receiverId=${receiverId}`;
    console.log('Sending request to:', url);
    
    // Using POST with query parameters as your controller expects
    return this.http.post<Connection>(url, {})
      .pipe(
        catchError(this.handleError)
      );
  }
  
  /**
   * Accept a connection request
   */
  acceptRequest(requestId: number): Observable<Connection> {
    const url = `${this.apiUrl}/accept/${requestId}`;
    console.log('Accepting request:', url);
    
    return this.http.put<Connection>(url, {})
      .pipe(
        catchError(this.handleError)
      );
  }
  
  /**
   * Reject a connection request
   */
  rejectRequest(requestId: number): Observable<Connection> {
    const url = `${this.apiUrl}/reject/${requestId}`;
    console.log('Rejecting request:', url);
    
    return this.http.put<Connection>(url, {})
      .pipe(
        catchError(this.handleError)
      );
  }
  
  /**
   * Get pending requests for a user
   */
  getPendingRequests(userId: number): Observable<Connection[]> {
    const url = `${this.apiUrl}/pending/${userId}`;
    console.log('Getting pending requests:', url);
    
    return this.http.get<Connection[]>(url)
      .pipe(
        catchError(this.handleError)
      );
  }
  
  /**
   * Get all connections for a user
   */
  getConnections(userId: number): Observable<Connection[]> {
    const url = `${this.apiUrl}/connections/${userId}`;
    console.log('Getting connections:', url);
    
    return this.http.get<Connection[]>(url)
      .pipe(
        catchError(this.handleError)
      );
  }
  
  /**
   * Error handler with detailed logging
   */
  private handleError(error: HttpErrorResponse) {
    console.error('Full error object:', error);
    
    let errorMessage = 'Unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
      console.error('Client Error:', error.error.message);
    } else {
      // Server-side error
      console.error('Server Error Details:', {
        status: error.status,
        message: error.message,
        url: error.url,
        error: error.error
      });
      
      // Handle different HTTP status codes with user-friendly messages
      switch (error.status) {
        case 0:
          errorMessage = '❌ Cannot connect to server. Make sure Spring Boot is running on port 8080';
          break;
        case 400:
          errorMessage = '❌ Bad request. Please check the user IDs.';
          break;
        case 404:
          errorMessage = `❌ API endpoint not found. Check if URL is correct: ${error.url}`;
          break;
        case 500:
          errorMessage = '❌ Server error. Check Spring Boot console for details.';
          // Try to extract backend error message
          if (error.error && typeof error.error === 'object') {
            if (error.error.error) {
              errorMessage = `❌ Server: ${error.error.error}`;
            } else if (error.error.message) {
              errorMessage = `❌ Server: ${error.error.message}`;
            }
          } else if (typeof error.error === 'string') {
            errorMessage = `❌ Server: ${error.error}`;
          }
          break;
        default:
          errorMessage = `❌ Error ${error.status}: ${error.message}`;
      }
    }
    
    // Log to console for debugging
    console.error('Final error message:', errorMessage);
    
    // Return observable with user-friendly error
    return throwError(() => new Error(errorMessage));
  }
}