import { Component } from '@angular/core';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-profile-edit',
  templateUrl: './profile-edit.component.html',
  styleUrls: ['./profile-edit.component.css']
})
export class ProfileEditComponent {

  profile: any = {
    user: { id: 1 }
  };

  constructor(private service: UserService) {}

  save() {
    this.service.updateProfile(this.profile)
      .subscribe(() => alert('Profile updated'));
  }
}
