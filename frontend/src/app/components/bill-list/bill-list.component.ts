import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BillService } from '../../services/bill.service';
import { SettingsService } from '../../services/settings.service';
import { ViewBillComponent } from '../view-bill/view-bill.component';

@Component({
  selector: 'app-bill-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, MatCardModule, MatIconModule, MatButtonModule, MatDialogModule, MatInputModule, MatFormFieldModule],
  templateUrl: './bill-list.component.html',
  styleUrls: ['./bill-list.component.css']
})
export class BillListComponent implements OnInit {
  bills: any[] = [];
  filteredBills: any[] = [];
  settings: any = null;
  searchQuery: string = '';

  constructor(
    private billService: BillService,
    private settingsService: SettingsService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.billService.getBills().subscribe(data => {
      this.bills = data.sort((a: any, b: any) => new Date(b.date).getTime() - new Date(a.date).getTime());
      this.filteredBills = [...this.bills];
    });
    this.settingsService.getSettings().subscribe(s => this.settings = s);
  }

  filterBills() {
    if (!this.searchQuery) {
      this.filteredBills = [...this.bills];
    } else {
      const q = this.searchQuery.toLowerCase();
      this.filteredBills = this.bills.filter(bill => 
        (bill.billNumber && bill.billNumber.toLowerCase().includes(q)) || 
        (bill.customerName && bill.customerName.toLowerCase().includes(q))
      );
    }
  }

  viewBill(bill: any) {
    this.dialog.open(ViewBillComponent, {
      width: '100%',
      maxWidth: '500px',
      data: { bill: bill, settings: this.settings },
      panelClass: 'custom-dialog-container'
    });
  }

  downloadPdf(id: string) {
    this.billService.downloadPdf(id).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `bill_${id}.pdf`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    });
  }

  shareWhatsApp(bill: any) {
    const text = `Hello ${bill.customerName},\nThank you for shopping with us.\nYour Bill No: ${bill.billNumber}\nAmount: ₹${bill.grandTotal}\nDate: ${new Date(bill.date).toLocaleDateString()}\nHave a great day!`;
    const waNumber = bill.mobileNumber.length === 10 ? '91' + bill.mobileNumber : bill.mobileNumber;

    this.billService.downloadPdf(bill.id).subscribe(async (blob) => {
      const file = new File([blob], `Bill_${bill.billNumber}.pdf`, { type: 'application/pdf' });
      
      if (navigator.canShare && navigator.canShare({ files: [file] })) {
        try {
          await navigator.share({
            title: `Bill ${bill.billNumber}`,
            text: text,
            files: [file]
          });
        } catch (e) {
          console.log('Share failed', e);
          window.open(`https://wa.me/${waNumber}?text=${encodeURIComponent(text)}`, '_blank');
        }
      } else {
        // Fallback for devices/browsers that do not support file sharing
        window.open(`https://wa.me/${waNumber}?text=${encodeURIComponent(text)}`, '_blank');
      }
    }, () => {
      // Fallback if PDF fails
      window.open(`https://wa.me/${waNumber}?text=${encodeURIComponent(text)}`, '_blank');
    });
  }
}
