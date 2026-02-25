import { Component } from '@angular/core';
import { NetworkService } from '../shared/services/network.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent {
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  
  // Sample user IDs for testing
  suggestions = [
    { id: 102, name: 'John Doe', title: 'Software Engineer' },
    { id: 103, name: 'Jane Smith', title: 'Product Manager' },
    { id: 104, name: 'Bob Johnson', title: 'UX Designer' }
  ];

  constructor(private networkService: NetworkService) {}

  sendConnectRequest(receiverId: number): void {
    const senderId = 1; // Existing User 1 in your DB
    
    // Reset messages
    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;
    
    console.log(`Sending request: senderId=${senderId}, receiverId=${receiverId}`);
    
    this.networkService.sendRequest(senderId, receiverId).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.successMessage = `✅ Connection request sent successfully to user ${receiverId}!`;
        console.log('Success response:', response);
        
        // Auto-hide success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (err: Error) => {
        this.isLoading = false;
        this.errorMessage = err.message;
        console.error('Request failed:', err);
        
        // Auto-hide error message after 5 seconds
        setTimeout(() => {
          this.errorMessage = '';
        }, 5000);
      }
    });
  }
  
  /**
   * Test method to check if backend is reachable
   */
  testBackendConnection(): void {
    console.log('🧪 Testing backend connection...');
    this.sendConnectRequest(102);
  }
  testDifferentIds() {
  console.log('=== Testing Different User ID Combinations ===');
  
  const testCases = [
    { sender: 1, receiver: 102, name: 'Test 1: User 1 → User 102' },
    { sender: 1, receiver: 103, name: 'Test 2: User 1 → User 103' },
    { sender: 1, receiver: 104, name: 'Test 3: User 1 → User 104' },
    { sender: 102, receiver: 1, name: 'Test 4: User 102 → User 1' },
    { sender: 999, receiver: 1, name: 'Test 5: Non-existent → User 1' },
    { sender: 1, receiver: 999, name: 'Test 6: User 1 → Non-existent' }
  ];
  
  // Test each combination with 2 second delay between tests
  testCases.forEach((test, index) => {
    setTimeout(() => {
      console.log(`\n${test.name}`);
      console.log(`Sending: senderId=${test.sender}, receiverId=${test.receiver}`);
      
      this.networkService.sendRequest(test.sender, test.receiver).subscribe({
        next: (response) => {
          console.log(`✅ SUCCESS for ${test.name}:`, response);
        },
        error: (err) => {
          console.log(`❌ FAILED for ${test.name}:`, err.message);
        }
      });
    }, index * 2000); // Wait 2 seconds between each test
  });
}
}