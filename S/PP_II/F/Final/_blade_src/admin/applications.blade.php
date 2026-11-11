@extends('layouts.admin')
@section('title', 'Applications — Admin')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:1rem">Seller Applications</h1>

@if($applications->isEmpty())
  <div class="empty-state">
    <div class="icon">📋</div>
    <h3>No pending applications</h3>
    <p>New seller applications will appear here for review.</p>
  </div>
@else
  @foreach($applications as $app)
    <div class="form-card">
      <div style="display:flex;justify-content:space-between;gap:1rem;flex-wrap:wrap;align-items:flex-start">
        <div>
          <h3>{{ $app->store_name }}</h3>
          <p style="color:var(--text-secondary);margin-top:.35rem">
            Owner: <strong>{{ $app->owner_name }}</strong> ·
            Phone: <span>{{ $app->phone }}</span> ·
            Location: <span>{{ $app->location }}</span>
          </p>
          @if($app->address)
            <p style="font-size:.9rem;margin-top:.35rem">{{ $app->address }}</p>
          @endif
          @if($app->business_info ?? null)
            <p style="font-size:.9rem;color:var(--text-muted);margin-top:.35rem">{{ $app->business_info }}</p>
          @endif
          <p style="font-size:.9rem;margin-top:.5rem">{{ $app->description }}</p>
          <p style="font-size:.85rem;color:var(--text-muted);margin-top:.35rem">{{ $app->user->email ?? '' }}</p>
        </div>
        <div style="display:flex;flex-direction:column;gap:.5rem;min-width:220px">
          <form action="{{ url('/admin/sellers/'.$app->id.'/approve') }}" method="post">
            @csrf
            <button type="submit" class="btn btn-primary btn-block" style="width:100%">Approve</button>
          </form>
          <form action="{{ url('/admin/sellers/'.$app->id.'/reject') }}" method="post">
            @csrf
            <input type="text" name="reason" required placeholder="Rejection reason"
                   style="width:100%;padding:.5rem;margin-bottom:.35rem;border:1px solid var(--border);border-radius:6px"/>
            <button type="submit" class="btn btn-outline btn-block" style="width:100%">Reject</button>
          </form>
        </div>
      </div>
    </div>
  @endforeach
@endif
@endsection
