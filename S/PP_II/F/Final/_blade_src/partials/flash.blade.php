@if(session('message'))
    <div class="container" style="padding-top:1rem"><div class="alert alert-ok">{{ session('message') }}</div></div>
@endif
@if(session('error'))
    <div class="container" style="padding-top:1rem"><div class="alert alert-err">{{ session('error') }}</div></div>
@endif
@if($errors->any())
    <div class="container" style="padding-top:1rem">
        <div class="alert alert-err">
            <ul style="margin:0;padding-left:1.1rem">
                @foreach($errors->all() as $e)<li>{{ $e }}</li>@endforeach
            </ul>
        </div>
    </div>
@endif
