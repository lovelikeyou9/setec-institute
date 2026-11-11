@extends('layouts.account')
@section('title', 'Notifications — MMT')
@section('content')
<div class="section-head" style="margin-bottom:1.25rem">
  <div>
    <h1 style="font-size:1.4rem">Notifications</h1>
    <p class="section-sub">Order updates, deals, and store messages</p>
  </div>
</div>

@if($notifications->isEmpty())
  <div class="empty-state">
    <div class="icon">🔔</div>
    <h3>You're all caught up</h3>
    <p>New notifications about orders and deals will appear here.</p>
  </div>
@else
  @foreach($notifications as $n)
    <div class="form-card" style="{{ !$n->read_flag ? 'border-left:3px solid var(--brand)' : '' }}">
      <div style="display:flex;justify-content:space-between;gap:1rem;flex-wrap:wrap">
        <div>
          <strong>{{ $n->title }}</strong>
          @unless($n->read_flag)
            <span class="status-pill blue" style="margin-left:.4rem">New</span>
          @endunless
          <p style="margin-top:.35rem;color:var(--text-secondary)">{{ $n->message }}</p>
          @if($n->link)
            <a href="{{ url('/account/notifications/'.$n->id.'/read') }}"
               onclick="event.preventDefault(); this.closest('.form-card').querySelector('form').submit();"
               style="font-size:.85rem;color:var(--brand)">View details →</a>
          @endif
          @unless($n->read_flag)
            <form method="post" action="{{ url('/account/notifications/'.$n->id.'/read') }}" style="margin-top:.5rem">
              @csrf
              <button class="btn btn-outline btn-sm" type="submit">Mark read{{ $n->link ? ' & open' : '' }}</button>
            </form>
          @endunless
        </div>
        <span style="font-size:.8rem;color:var(--text-muted);white-space:nowrap">
          {{ optional($n->created_at)->format('d M H:i') }}
        </span>
      </div>
    </div>
  @endforeach
@endif
@endsection
