

<h1>Update Existing Product: </h1>
<form action="/products/8" method="post">
    @csrf
    @method("put")
    {{-- <input type="hidden" name="_method" value="put"> --}}
    <input type="submit" value="Submit" name="buttonSubmit" id="buttonSubmit">

</form>
