import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private API = 'http://localhost:8080/api/profile';

  constructor(private http: HttpClient) {}

  createProfile(data:any) {
    return this.http.post(this.API, data);
  }

  getMyProfile() {
    return this.http.get(`${this.API}/me`);
  }

  updateProfile(data:any) {
    return this.http.put(this.API, data);
  }

  deleteProfile(id:number) {
    return this.http.delete(`${this.API}/${id}`);
  }

  getAllProfiles() {
    return this.http.get(this.API);
  }

  search(name:string) {
    return this.http.get(`${this.API}/search?name=${name}`);
  }
}
