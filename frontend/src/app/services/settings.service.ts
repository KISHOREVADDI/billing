import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private apiUrl = 'http://localhost:8080/api/settings';

  constructor(private http: HttpClient) { }

  getSettings() {
    return this.http.get<any>(this.apiUrl);
  }

  saveSettings(settings: any) {
    return this.http.post<any>(this.apiUrl, settings);
  }
}
