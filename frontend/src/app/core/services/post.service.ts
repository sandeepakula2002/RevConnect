import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse, PageResponse, Post, CreatePostRequest, PostAnalytics
} from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class PostService {

  private readonly API = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}

  createPost(data: CreatePostRequest): Observable<ApiResponse<Post>> {
    return this.http.post<ApiResponse<Post>>(this.API, data);
  }

  getPost(id: number): Observable<ApiResponse<Post>> {
    return this.http.get<ApiResponse<Post>>(`${this.API}/${id}`);
  }

  updatePost(id: number, data: Partial<Post>): Observable<ApiResponse<Post>> {
    return this.http.put<ApiResponse<Post>>(`${this.API}/${id}`, data);
  }

  deletePost(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/${id}`);
  }

  getUserPosts(userId: number, page = 0, size = 10): Observable<ApiResponse<PageResponse<Post>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<Post>>>(`${this.API}/user/${userId}`, { params });
  }

  getTrendingPosts(): Observable<ApiResponse<Post[]>> {
    return this.http.get<ApiResponse<Post[]>>(`${this.API}/trending`);
  }

  searchByHashtag(hashtag: string, page = 0): Observable<ApiResponse<PageResponse<Post>>> {
    const params = new HttpParams().set('hashtag', hashtag).set('page', page);
    return this.http.get<ApiResponse<PageResponse<Post>>>(`${this.API}/search`, { params });
  }

  likePost(postId: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.API}/${postId}/like`, {});
  }

  unlikePost(postId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.API}/${postId}/like`);
  }

  getAnalytics(postId: number): Observable<ApiResponse<PostAnalytics>> {
    return this.http.get<ApiResponse<PostAnalytics>>(`${this.API}/${postId}/analytics`);
  }

  addComment(postId: number, content: string): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${environment.apiUrl}/posts/${postId}/comments`, { content });
  }

  getComments(postId: number, page = 0): Observable<ApiResponse<PageResponse<Comment>>> {
    return this.http.get<ApiResponse<PageResponse<Comment>>>(
      `${environment.apiUrl}/posts/${postId}/comments?page=${page}`
    );
  }

  deleteComment(commentId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${environment.apiUrl}/comments/${commentId}`);
  }

  repost(originalPostId: number, content: string): Observable<ApiResponse<Post>> {
    return this.createPost({ content, originalPostId, type: 'REPOST' });
  }
}
