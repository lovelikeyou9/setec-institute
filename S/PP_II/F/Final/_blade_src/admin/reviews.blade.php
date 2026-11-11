@extends('layouts.admin')
@section('title', 'Reviews — Admin')
@section('content')
<h1 style="font-size:1.4rem;margin-bottom:1rem">Reviews</h1>

@if($reviews->isEmpty())
  <div class="empty-state">
    <h3>No reviews</h3>
    <p>Customer product reviews will be listed here.</p>
  </div>
@else
  <div class="data-table-wrap">
    <table class="data-table">
      <thead>
        <tr>
          <th>Product</th>
          <th>User</th>
          <th>Rating</th>
          <th>Comment</th>
          <th>Verified</th>
          <th>Hidden</th>
          <th>Date</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        @foreach($reviews as $r)
          <tr>
            <td>{{ $r->product->name ?? 'Product' }}</td>
            <td>{{ $r->user->full_name ?? '—' }}</td>
            <td>{{ $r->rating }} &#9733;</td>
            <td style="max-width:280px">{{ \Illuminate\Support\Str::limit($r->comment, 100) }}</td>
            <td>
              <span class="status-pill {{ $r->verified_purchase ? 'green' : 'gray' }}">
                {{ $r->verified_purchase ? 'Yes' : 'No' }}
              </span>
            </td>
            <td>
              <span class="status-pill {{ $r->hidden ? 'red' : 'green' }}">
                {{ $r->hidden ? 'Hidden' : 'Visible' }}
              </span>
            </td>
            <td>{{ optional($r->created_at)->format('d M Y') ?: '—' }}</td>
            <td class="table-actions">
              @unless($r->hidden)
                <form method="post" action="{{ url('/admin/reviews/'.$r->id.'/hide') }}">@csrf
                  <button class="btn btn-outline btn-sm" type="submit">Hide</button>
                </form>
              @endunless
            </td>
          </tr>
        @endforeach
      </tbody>
    </table>
  </div>
@endif
@endsection
