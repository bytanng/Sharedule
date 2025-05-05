import React, { useState,useEffect } from "react";
import { Footer, Navbar } from "../components";
import "bootstrap-icons/font/bootstrap-icons.css";
import { register } from "../utils/UserRoutes";
import DataTable from 'react-data-table-component';
import { getAllUsers } from "../utils/UserRoutes";

const Admin = () => {
  const [users, setUsers] = useState([]);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [role,setRole] = useState("USER");
  const [errorMessage, setErrorMessage] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [editUser, setEditUser] = useState(null);
  const [deleteUser, setDeleteUser] = useState(null);
  const [editedUsername, setEditedUsername] = useState("");
  const [editedEmail, setEditedEmail] = useState("");
  const [editedRole, setEditedRole] = useState("");
  const [loading, setLoading] = useState(false);


  const columns = [
    { name: 'Username', selector: (row) => row.username, sortable: true },
    { name: 'Email', selector: (row) => row.email, sortable: true },
    { name: 'Role', selector: (row) => row.role, sortable: true },
    {
      name: 'Actions',
      cell: (row) => (
        <>
          <button type="button" className="btn btn-warning mx-2" onClick={() => handleEditClick(row)}>
            Edit User
          </button>
          <button type="button" className="btn btn-danger mx-2" onClick={() => handleDeleteClick(row)}>
            Delete User
          </button>
        </>
      ),
      ignoreRowClick: true,
      allowOverflow: true,
    },
  ];
  const validateForm = () => {
    // Reset error message
    setErrorMessage("");

    // Check if passwords match
    if (password !== confirmPassword) {
      setErrorMessage("Passwords do not match");
      return false;
    }

    // Check password length
    if (password.length < 3) {
      setErrorMessage("Password must be at least 3 characters long");
      return false;
    }

    return true;
  };
  const handleEditClick = (user) => {
    setEditUser(user);
    setEditedUsername(user.username);
    setEditedEmail(user.email);
    setEditedRole(user.role);
  };

  const handleSaveEdit = () => {
    if (editUser) {
      setUsers(users.map(u =>
        u.id === editUser.id ? { ...u, username: editedUsername, email: editedEmail, role: editedRole } : u
      ));
      setEditUser(null);
    }
  };

  const handleDeleteClick = (user) => {
    setDeleteUser(user);
  };

  const handleConfirmDelete = () => {
    if (deleteUser) {
      setUsers(users.filter(u => u.id !== deleteUser.id));
      setDeleteUser(null);
    }
  };

  const handleCreateClick = async (e) => {
    e.preventDefault();

    // Validate form before submission
    if (!validateForm()) {
      return;
    }
    console.log(role);
    const user = { role,username, email, password };

    setLoading(true);
    setErrorMessage("");
    console.log(user);
    const result = await register(user);

    setLoading(false);

    if (!result.startsWith("User successfully registered")) {
      setErrorMessage(result);
    } else {
    }
  };

  const filteredUsers = users.filter((user) =>
    Object.values(user).some((value) =>
      String(value).toLowerCase().includes(searchQuery.toLowerCase())
    )
  );

  
  useEffect(() => {
    const token = localStorage.getItem("token");
    getAllUsers(token)
      .then(users => {
        setUsers(users); 
        
      })
      .catch(error => {
        console.error("Error fetching users:", error);
      });
  }, []);
  return (
    <>
      <Navbar />
      <div className="container my-3 py-3">
        <h1 className="text-center">Admin Management</h1>
        <hr />
        <div className="row my-4 h-100">
          <div className="col-md-4 mb-4">
            <input
              type="text search"
              placeholder="Search Users..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="form-control w-full"
            />
          </div>
          <div className="col-12">
            <button type="button" class="btn btn-success float-right mx-2" data-toggle="modal" data-target="#createUserModal">
              Create User
            </button>
            <DataTable
              columns={columns}
              data={filteredUsers}
              pagination
              paginationPerPage={itemsPerPage}
              paginationRowsPerPageOptions={[5, 10, 20, filteredUsers.length]}
              onRowsPerPageChange={(perPage) => setItemsPerPage(perPage)}
              filter
              filterText={searchQuery}
              onFilterChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>
      </div>
      <Footer />
      <>
      <div className="modal fade" id="createUserModal" tabIndex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
        <div className="modal-dialog modal-dialog-centered" role="document">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title" id="exampleModalLongTitle">Create User</h5>
              <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div className="modal-body">
              <form onSubmit={handleCreateClick}>
                <div className="form-group my-3">
                  <label htmlFor="Name">Username</label>
                  <input
                    type="text"
                    className="form-control"
                    id="Name"
                    placeholder="Enter Your Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                  />
                </div>
                <div className="form my-3">
                  <label htmlFor="Email">Email address</label>
                  <input
                    type="email"
                    className="form-control"
                    id="Email"
                    placeholder="name@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                <div className="form my-3">
                  <label htmlFor="Password">Password</label>
                  <input
                    type="password"
                    className="form-control"
                    id="Password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                  <small className="form-text text-muted">
                    Password must be at least 3 characters long
                  </small>
                </div>
                <div className="form my-3">
                  <label htmlFor="ConfirmPassword">Confirm Password</label>
                  <input
                    type="password"
                    className="form-control"
                    id="ConfirmPassword"
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group my-3">
                  <label htmlFor="role">Role</label> 
                  <select
                    className="form-select"
                    id="role"
                    aria-label="Select Role"
                    value={role} 
                    onChange={(e) => setRole(e.target.value)}
                  >
                    <option value="USER" defaultValue>User</option>
                    <option value="ADMIN">Admin</option>
                  </select>
                </div>
                {errorMessage && (
                  <div className="alert alert-danger" role="alert">
                    {errorMessage}
                  </div>
                )}
                <button
                    className="my-2 mx-auto btn btn-dark"
                    type="submit"
                  >
                    {loading ? "Registering..." : "Register"}
                </button>
              </form>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    </>

    </>
  );
};

export default Admin;
