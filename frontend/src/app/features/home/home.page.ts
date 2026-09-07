import { AfterViewInit, Component, ElementRef, OnDestroy, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink],
  templateUrl: './home.page.html',
  host: { class: 'page-shell' },
})
export class HomePage implements AfterViewInit, OnDestroy {
  private readonly host = inject(ElementRef<HTMLElement>);
  private readonly router = inject(Router);
  readonly auth = inject(AuthService);
  readonly year = new Date().getFullYear();

  readonly previewDays = [
    { label: 'S', date: 8, active: false },
    { label: 'M', date: 9, active: false },
    { label: 'T', date: 10, active: true },
    { label: 'W', date: 11, active: true },
    { label: 'T', date: 12, active: true },
    { label: 'F', date: 13, active: false },
    { label: 'S', date: 14, active: false },
  ];

  private observer: IntersectionObserver | null = null;

  ngAfterViewInit(): void {
    const root = this.host.nativeElement;
    const targets = root.querySelectorAll('.reveal');
    this.observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-visible');
            this.observer?.unobserve(entry.target);
          }
        }
      },
      { root, rootMargin: '0px 0px -8% 0px', threshold: 0.18 },
    );
    targets.forEach((el: Element) => this.observer?.observe(el));
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }

  logout(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/');
  }

  scrollToFeatures(event: Event): void {
    this.scrollToSection(event, 'workspaces');
  }

  scrollToSection(event: Event, sectionId: string): void {
    event.preventDefault();
    this.host.nativeElement
      .querySelector(`#${sectionId}`)
      ?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}
