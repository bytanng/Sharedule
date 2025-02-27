import React, { useEffect } from 'react'
import { NavLink } from 'react-router-dom'
import { useSelector } from 'react-redux'

const Navbar = () => {
    const state = useSelector(state => state.handleCart)
    
    useEffect(() => {
        // Add hover functionality to dropdowns
        const dropdownElements = document.querySelectorAll('.dropdown');
        
        dropdownElements.forEach(dropdown => {
            dropdown.addEventListener('mouseenter', () => {
                dropdown.querySelector('.dropdown-menu').classList.add('show');
            });
            
            dropdown.addEventListener('mouseleave', () => {
                dropdown.querySelector('.dropdown-menu').classList.remove('show');
            });
        });
        
        // Cleanup event listeners on component unmount
        return () => {
            dropdownElements.forEach(dropdown => {
                dropdown.removeEventListener('mouseenter', () => {});
                dropdown.removeEventListener('mouseleave', () => {});
            });
        };
    }, []);
    
    return (
        <nav className="navbar navbar-expand-lg navbar-light bg-light py-3 sticky-top">
            <div className="container">
                <NavLink className="navbar-brand fw-bold fs-4 px-2" to="/"> Sharedule</NavLink>
                <button className="navbar-toggler mx-2" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav m-auto my-2 text-center">
                        <li className="nav-item">
                            <NavLink className="nav-link" to="/">Home </NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className="nav-link" to="/product">Products</NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className="nav-link" to="/about">About</NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className="nav-link" to="/contact">Contact</NavLink>
                        </li>
                    </ul>
                    <div className="buttons text-center">
                        <NavLink to="/login" className="btn btn-outline-dark m-2"><i className="fa fa-sign-in-alt mr-1"></i> Login</NavLink>
                        <NavLink to="/register" className="btn btn-outline-dark m-2"><i className="fa fa-user-plus mr-1"></i> Register</NavLink>
                        <div className="dropdown d-inline-block">
                            <button className="btn btn-outline-dark m-2 dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                <i className="fa fa-user mr-1"></i> User123
                            </button>
                            <ul className="dropdown-menu" aria-labelledby="userDropdown">
                                <li><NavLink className="dropdown-item" to="/profile"><i className="fa fa-user-circle me-2"></i>My Profile</NavLink></li>
                                <li><hr className="dropdown-divider" /></li>
                                <li><NavLink className="dropdown-item" to="/logout"><i className="fa fa-sign-out-alt me-2"></i>Log Out</NavLink></li>
                            </ul>
                        </div>
                    </div>
                </div>


            </div>
            
            <style jsx>{`
                .dropdown:hover .dropdown-menu {
                    display: block;
                    margin-top: 0;
                }
            `}</style>
        </nav>
    )
}

export default Navbar