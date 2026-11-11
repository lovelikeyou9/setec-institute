@props(['status' => '', 'type' => 'order'])
@php
  $label = str_replace('_', ' ', (string) $status);
  $tone = 'blue';
  if ($type === 'payout') {
    $tone = match ($status) {
      'RELEASED', 'PAID' => 'green',
      'REFUNDED', 'CANCELLED' => 'red',
      'HELD', 'IN_PROTECTION' => 'amber',
      default => 'blue',
    };
  } else {
    $tone = match ($status) {
      'DELIVERED', 'COMPLETED', 'PAID_OUT' => 'green',
      'CANCELLED', 'REFUNDED', 'DISPUTED', 'REJECTED' => 'red',
      'PENDING_PAYMENT', 'PAYMENT_CONFIRMED', 'FUNDS_HELD', 'SELLER_PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY', 'PENDING' => 'amber',
      'APPROVED' => 'green',
      default => 'blue',
    };
  }
@endphp
<span {{ $attributes->merge(['class' => 'status-pill '.$tone]) }}>{{ $label }}</span>
