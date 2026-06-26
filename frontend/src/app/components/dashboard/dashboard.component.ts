import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { BillService } from '../../services/bill.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatIconModule, MatButtonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  todaySales = 0;
  todayBillsCount = 0;
  monthlyRevenue = 0;

  constructor(private billService: BillService) {}

  ngOnInit(): void {
    this.billService.getBills().subscribe(bills => {
      const today = new Date();
      const todayStr = today.toDateString();
      const thisMonthStr = today.getMonth() + '-' + today.getFullYear();
      
      this.todayBillsCount = bills.filter((b: any) => new Date(b.date).toDateString() === todayStr).length;
      this.todaySales = bills.filter((b: any) => new Date(b.date).toDateString() === todayStr)
                             .reduce((acc: number, curr: any) => acc + curr.grandTotal, 0);
      this.monthlyRevenue = bills.filter((b: any) => (new Date(b.date).getMonth() + '-' + new Date(b.date).getFullYear()) === thisMonthStr)
                                 .reduce((acc: number, curr: any) => acc + curr.grandTotal, 0);
    });
  }
}
