import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { BillService } from '../../services/bill.service';

@Component({
  selector: 'app-create-bill',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatIconModule, MatDividerModule
  ],
  templateUrl: './create-bill.component.html',
  styleUrls: ['./create-bill.component.css']
})
export class CreateBillComponent {
  bill = {
    customerName: '',
    mobileNumber: '',
    items: [
      { itemName: '', priceCode: '', unit: 'Pcs', quantity: 1, rate: 0, amount: 0 }
    ],
    subtotal: 0,
    discountPercent: 0,
    discount: 0,
    gstPercent: 0,
    gst: 0,
    grandTotal: 0
  };

  constructor(private billService: BillService, private router: Router) {}

  addItem() {
    this.bill.items.push({ itemName: '', priceCode: '', unit: 'Pcs', quantity: 1, rate: 0, amount: 0 });
    this.calculateTotals();
  }

  removeItem(index: number) {
    if (this.bill.items.length > 1) {
      this.bill.items.splice(index, 1);
      this.calculateTotals();
    }
  }

  updateItemAmount(index: number) {
    const item = this.bill.items[index];
    item.amount = (item.quantity || 0) * (item.rate || 0);
    this.calculateTotals();
  }

  changeQuantity(index: number, change: number) {
    const item = this.bill.items[index];
    if ((item.quantity || 0) + change > 0) {
      item.quantity = (item.quantity || 0) + change;
      this.updateItemAmount(index);
    }
  }

  calculateTotals() {
    this.bill.subtotal = this.bill.items.reduce((sum, item) => sum + (item.amount || 0), 0);
    this.bill.discount = (this.bill.subtotal * (this.bill.discountPercent || 0)) / 100;
    const afterDiscount = this.bill.subtotal - this.bill.discount;
    this.bill.gst = (afterDiscount * (this.bill.gstPercent || 0)) / 100;
    this.bill.grandTotal = Math.round(afterDiscount + this.bill.gst);
  }

  generateBill() {
    const payload = {
      ...this.bill,
      customerName: this.bill.customerName ? this.bill.customerName.toUpperCase() : '',
      discountPercent: this.bill.discountPercent || 0,
      gstPercent: this.bill.gstPercent || 0,
      discount: this.bill.discount || 0,
      gst: this.bill.gst || 0,
      subtotal: this.bill.subtotal || 0,
      grandTotal: this.bill.grandTotal || 0,
      items: this.bill.items.map(item => ({
        ...item,
        priceCode: item.priceCode ? item.priceCode.toUpperCase() : '',
        quantity: item.quantity || 1,
        rate: item.rate || 0,
        amount: item.amount || 0
      }))
    };

    this.billService.createBill(payload).subscribe({
      next: (res) => {
        alert('Bill generated successfully! Bill No: ' + res.billNumber);

        if (this.bill.mobileNumber) {
          const customerName = this.bill.customerName || 'Valued Customer';
          const text = `Hello ${customerName},\n\nThank you for shopping with us! Your bill (No: ${res.billNumber}) for the amount of ₹${this.bill.grandTotal} has been generated.\n\nVisit again!`;
          const waNumber = this.bill.mobileNumber.length === 10 ? '91' + this.bill.mobileNumber : this.bill.mobileNumber;
          
          this.billService.downloadPdf(res.id).subscribe(async (blob) => {
            const file = new File([blob], `Bill_${res.billNumber}.pdf`, { type: 'application/pdf' });
            if (navigator.canShare && navigator.canShare({ files: [file] })) {
              try {
                await navigator.share({
                  title: `Bill ${res.billNumber}`,
                  text: text,
                  files: [file]
                });
              } catch (e) {
                console.log('Share failed', e);
                window.open(`https://wa.me/${waNumber}?text=${encodeURIComponent(text)}`, '_blank');
              }
            } else {
              window.open(`https://wa.me/${waNumber}?text=${encodeURIComponent(text)}`, '_blank');
            }
            this.router.navigate(['/bills']);
          }, () => {
            window.open(`https://wa.me/${waNumber}?text=${encodeURIComponent(text)}`, '_blank');
            this.router.navigate(['/bills']);
          });
        } else {
          this.router.navigate(['/bills']);
        }
      },
      error: (err) => {
        console.error('Failed to create bill:', err);
        alert('Failed to generate bill. Please check if all fields are filled properly.');
      }
    });
  }
}
