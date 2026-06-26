import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BillService {
  private apiUrl = 'http://localhost:8080/api/bills';

  constructor(private http: HttpClient) { }

  createBill(bill: any) {
    return this.http.post<any>(this.apiUrl, bill);
  }

  getBills() {
    return this.http.get<any[]>(this.apiUrl);
  }

  downloadPdf(id: string) {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, { responseType: 'blob' });
  }

  generateQr(amount: number) {
    return this.http.get<any>(`${this.apiUrl}/qr?amount=${amount}`);
  }
}
