import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private apiUrl = 'https://billing-f81b.onrender.com/api/settings';

  constructor(private http: HttpClient) { }

  getSettings() {
    return this.http.get<any>(this.apiUrl);
  }

  saveSettings(settings: any) {
    return this.http.post<any>(this.apiUrl, settings);
  }
}
