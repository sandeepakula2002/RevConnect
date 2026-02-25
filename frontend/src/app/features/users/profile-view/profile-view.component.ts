import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {

  profile: any;

  constructor(private service: UserService) {}

  ngOnInit(): void {
    this.service.getProfile(1).subscribe(res => this.profile = res);
  }
}
