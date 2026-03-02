import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  PageResponse,
  Post,
  CreatePostRequest,
  PostAnalytics
} from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class PostService {

  private readonly API = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}

  // ================= CREATE =================
  createPost(data: CreatePostRequest): Observable<ApiResponse<Post>> {
    return this.http.post<ApiResponse<Post>>(this.API, data);
  }

  // ================= GET SINGLE =================
  getPost(id: number): Observable<ApiResponse<Post>> {
    return this.http.get<ApiResponse<Post>>(`${this.API}/${id}`);
  }

  // ================= UPDATE =================
  updatePost(id: number, data: Partial<Post>): Observable<ApiResponse<Post>> {
    return this.http.put<ApiResponse<Post>>(`${this.API}/${id}`, data);
  }

  // ================= DELETE =================
  deletePost(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/${id}`);
  }

  // ================= USER POSTS =================
  getUserPosts(
    userId: number,
    page: number = 0,
    size: number = 10
  ): Observable<ApiResponse<PageResponse<Post>>> {

    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<ApiResponse<PageResponse<Post>>>(
      `${this.API}/user/${userId}`,
      { params }
    );
  }

  // ================= TRENDING =================
  getTrendingPosts(): Observable<ApiResponse<Post[]>> {
    return this.http.get<ApiResponse<Post[]>>(`${this.API}/trending`);
  }

  // ================= SEARCH BY HASHTAG =================
  searchByHashtag(
    hashtag: string,
    page: number = 0
  ): Observable<ApiResponse<PageResponse<Post>>> {

    const params = new HttpParams()
      .set('hashtag', hashtag)
      .set('page', page);

    return this.http.get<ApiResponse<PageResponse<Post>>>(
      `${this.API}/search`,
      { params }
    );
  }

  // ================= LIKE =================
  likePost(postId: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(
      `${this.API}/${postId}/like`,
      {}
    );
  }

  // ================= UNLIKE =================
  unlikePost(postId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(
      `${this.API}/${postId}/like`
    );
  }

  // ================= ANALYTICS =================
  getAnalytics(postId: number): Observable<ApiResponse<PostAnalytics>> {
    return this.http.get<ApiResponse<PostAnalytics>>(
      `${this.API}/${postId}/analytics`
    );
  }

  // ================= COMMENTS =================
  addComment(postId: number, content: string): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(
      `${this.API}/${postId}/comments`,
      { content }
    );
  }

  getComments(
    postId: number,
    page: number = 0
  ): Observable<ApiResponse<PageResponse<any>>> {

    const params = new HttpParams().set('page', page);

    return this.http.get<ApiResponse<PageResponse<any>>>(
      `${this.API}/${postId}/comments`,
      { params }
    );
  }

  deleteComment(commentId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${environment.apiUrl}/comments/${commentId}`
    );
  }

  // ================= REPOST =================
  repost(originalPostId: number, content: string): Observable<ApiResponse<Post>> {
    return this.createPost({
      content,
      originalPostId,
      type: 'REPOST'
    });
  }
}
