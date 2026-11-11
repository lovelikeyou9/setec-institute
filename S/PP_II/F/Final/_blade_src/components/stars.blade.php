@props(['rating' => 0])
@php $full = max(0, min(5, (int) round((float) $rating))); @endphp
<span class="stars" {{ $attributes }} aria-label="Rating {{ number_format((float) $rating, 1) }} out of 5">
  @for($i = 1; $i <= 5; $i++)
    <span class="{{ $i <= $full ? 'on' : '' }}">&#9733;</span>
  @endfor
</span>
