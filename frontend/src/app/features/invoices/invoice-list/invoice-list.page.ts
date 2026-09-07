import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { extractErrorMessage } from '../../../core/api/extract-error-message';
import {
  INVOICE_STATUSES,
  Invoice,
  InvoiceStatus,
  formatMoney,
  invoiceStatusMeta,
} from '../../bookings/booking.models';
import { InvoiceService } from '../invoice.service';

@Component({
  selector: 'app-invoice-list-page',
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './invoice-list.page.html',
  host: { class: 'page-shell page-shell--fill' },
})
export class InvoiceListPage implements OnInit {
  private readonly invoiceService = inject(InvoiceService);
  readonly auth = inject(AuthService);

  readonly invoices = signal<Invoice[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly statusFilter = signal<InvoiceStatus | ''>('');
  readonly userIdFilter = signal('');
  readonly bookingIdFilter = signal('');
  readonly formatMoney = formatMoney;
  readonly statusMeta = invoiceStatusMeta;
  readonly statusOptions = INVOICE_STATUSES;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    const request = this.auth.isAdmin()
      ? this.invoiceService.listAll({
          status: this.statusFilter(),
          userId: this.userIdFilter(),
          bookingId: this.bookingIdFilter(),
        })
      : this.invoiceService.listMine(this.statusFilter());

    request.subscribe({
      next: (invoices) => {
        this.invoices.set(invoices);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(extractErrorMessage(err, 'Failed to load invoices'));
        this.loading.set(false);
      },
    });
  }

  applyFilters(): void {
    this.load();
  }

  clearFilters(): void {
    this.statusFilter.set('');
    this.userIdFilter.set('');
    this.bookingIdFilter.set('');
    this.load();
  }
}
