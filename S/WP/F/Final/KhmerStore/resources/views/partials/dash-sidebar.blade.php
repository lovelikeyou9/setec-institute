@auth
  @if(Auth::user()->role === 'ADMIN')
    @include('partials.admin-sidebar')
  @elseif(Auth::user()->role === 'SELLER')
    @include('partials.seller-sidebar')
  @else
    @include('partials.account-sidebar')
  @endif
@else
  @include('partials.account-sidebar')
@endauth
