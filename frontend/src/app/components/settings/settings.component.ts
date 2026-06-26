import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SettingsService } from '../../services/settings.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatIconModule, MatSnackBarModule
  ],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  settings: any = {};

  constructor(private settingsService: SettingsService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.settingsService.getSettings().subscribe(data => {
      if (data) {
        this.settings = data;
      }
    });
  }

  saveSettings() {
    this.settingsService.saveSettings(this.settings).subscribe(data => {
      this.snackBar.open('Settings saved successfully', 'Close', { duration: 3000 });
      this.settings = data;
    });
  }
}
