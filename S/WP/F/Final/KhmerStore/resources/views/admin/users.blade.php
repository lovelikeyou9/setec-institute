@extends('layouts.admin')
@section('title', 'Users — Admin')
@section('content')
<div class="section-head" style="margin-bottom:1rem">
  <h1 style="font-size:1.4rem">Users Lists</h1>
  <button type="button" class="btn btn-primary" data-modal-open="modalAddUser">+ Add User</button>
</div>

<form class="dash-search" action="{{ url('/admin/users') }}" method="get">
  <input type="search" name="q" value="{{ request('q') }}" placeholder="Search name, email, phone..."/>
  <label class="dash-sort">
    <span>Role</span>
    <select name="role" onchange="this.form.submit()">
      <option value="" @selected(!request('role'))>All roles</option>
      <option value="CUSTOMER" @selected(request('role')==='CUSTOMER')>Customer</option>
      <option value="SELLER" @selected(request('role')==='SELLER')>Seller</option>
      <option value="ADMIN" @selected(request('role')==='ADMIN')>Admin</option>
    </select>
  </label>
  <label class="dash-sort">
    <span>Sort</span>
    <select name="sort" onchange="this.form.submit()">
      <option value="newest" @selected(request('sort','newest')==='newest')>Newest</option>
      <option value="oldest" @selected(request('sort')==='oldest')>Oldest</option>
      <option value="name_asc" @selected(request('sort')==='name_asc')>Name A–Z</option>
      <option value="name_desc" @selected(request('sort')==='name_desc')>Name Z–A</option>
    </select>
  </label>
  <button type="submit" class="btn btn-primary btn-sm">Search</button>
  @if(request('q') || request('role') || (request('sort') && request('sort') !== 'newest'))
    <a href="{{ url('/admin/users') }}{{ request('role') ? '?role='.urlencode(request('role')) : '' }}" class="btn btn-outline btn-sm">Clear</a>
  @endif
</form>

<div class="data-table-wrap">
  <table class="data-table">
    <thead>
      <tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Joined</th><th>Actions</th></tr>
    </thead>
    <tbody>
      @foreach($users as $u)
        <tr>
          <td>{{ $u->id }}</td>
          <td>{{ $u->full_name }}</td>
          <td>{{ $u->email }}</td>
          <td>{{ $u->phone ?? '—' }}</td>
          <td><span class="status-pill blue">{{ $u->role }}</span></td>
          <td>
            <span class="status-pill {{ $u->enabled ? 'green' : 'red' }}">{{ $u->enabled ? 'Active' : 'Banned' }}</span>
          </td>
          <td>{{ optional($u->created_at)->format('d M Y') ?: '—' }}</td>
          <td class="table-actions">
            @if($u->role !== 'ADMIN')
              <button type="button" class="btn btn-outline btn-sm"
                      data-edit="editUserForm"
                      data-modal-open="modalEditUser"
                      data-action="{{ url('/admin/users/'.$u->id.'/edit') }}"
                      data-field-fullName="{{ $u->full_name }}"
                      data-field-email="{{ $u->email }}"
                      data-field-phone="{{ $u->phone }}"
                      data-field-role="{{ $u->role }}"
                      data-field-enabled="{{ $u->enabled ? 'true' : 'false' }}">Edit</button>
              @if($u->enabled)
                <form action="{{ url('/admin/users/'.$u->id.'/ban') }}" method="post" style="display:inline" title="Disable account — cannot log in">@csrf
                  <button type="submit" class="btn btn-sm btn-danger">Ban login</button>
                </form>
              @else
                <form action="{{ url('/admin/users/'.$u->id.'/unban') }}" method="post" style="display:inline" title="Re-enable account login">@csrf
                  <button type="submit" class="btn btn-sm btn-primary">Unban login</button>
                </form>
              @endif
              <button type="button" class="btn btn-danger btn-sm"
                      data-delete="{{ url('/admin/users/'.$u->id.'/delete') }}"
                      data-label="user {{ $u->full_name }}">Delete</button>
            @else
              <span style="color:var(--text-muted);font-size:.8rem">Protected</span>
            @endif
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>
</div>
{{ $users->links() }}
@endsection

@section('modals')
<div class="modal-backdrop" id="modalAddUser">
  <div class="modal">
    <div class="modal-header">
      <h3>Add User</h3>
      <button type="button" class="modal-close" data-modal-close>&times;</button>
    </div>
    <form action="{{ url('/admin/users') }}" method="post">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Full Name *</label><input type="text" name="fullName" required/></div>
          <div class="form-group"><label>Email *</label><input type="email" name="email" required/></div>
          <div class="form-group"><label>Phone</label><input type="text" name="phone"/></div>
          <div class="form-group"><label>Password *</label><input type="password" name="password" required minlength="6"/></div>
          <div class="form-group">
            <label>Role *</label>
            <select name="role" required>
              <option value="CUSTOMER">CUSTOMER</option>
              <option value="SELLER">SELLER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <div class="form-group"><label><input type="checkbox" name="enabled" value="true" checked/> Active</label></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Add User</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalEditUser">
  <div class="modal">
    <div class="modal-header">
      <h3>Edit User</h3>
      <button type="button" class="modal-close" data-modal-close>&times;</button>
    </div>
    <form id="editUserForm" method="post" action="#">
      @csrf
      <div class="modal-body">
        <div class="form-grid">
          <div class="form-group"><label>Full Name *</label><input type="text" name="fullName" required/></div>
          <div class="form-group"><label>Email *</label><input type="email" name="email" required/></div>
          <div class="form-group"><label>Phone</label><input type="text" name="phone"/></div>
          <div class="form-group"><label>New Password</label><input type="password" name="password" placeholder="Leave blank to keep"/></div>
          <div class="form-group">
            <label>Role *</label>
            <select name="role" required>
              <option value="CUSTOMER">CUSTOMER</option>
              <option value="SELLER">SELLER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <div class="form-group"><label><input type="checkbox" name="enabled" value="true"/> Active</label></div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Changes</button>
      </div>
    </form>
  </div>
</div>

<div class="modal-backdrop" id="modalDelete">
  <div class="modal">
    <div class="modal-header">
      <h3>Confirm Delete</h3>
      <button type="button" class="modal-close" data-modal-close>&times;</button>
    </div>
    <form id="deleteForm" method="post" action="#">
      @csrf
      <div class="modal-body">
        <p>Are you sure you want to delete <strong id="deleteLabel">this item</strong>?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-danger">Delete</button>
      </div>
    </form>
  </div>
</div>
@endsection
