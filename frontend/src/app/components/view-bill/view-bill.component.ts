import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-view-bill',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  templateUrl: './view-bill.component.html',
  styleUrls: ['./view-bill.component.css']
})
export class ViewBillComponent {
  constructor(
    public dialogRef: MatDialogRef<ViewBillComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { bill: any, settings: any }
  ) {}

  printBill() {
    window.print();
  }
}
