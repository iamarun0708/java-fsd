import React, { Component } from 'react';
import './style.css'
class Cart extends Component {
    render() {
        return (
            <table border='1' className='table'>
                <thead>
                    <tr>
                        <th>Item Name</th>
                        <th>Price</th>
                    </tr>
                </thead>
                <tbody>{
                    this.props.item.map((item, index) => {
                        return (
                            <tr key={index}>
                                <td>{item.itemname}</td>
                                <td>{item.price}</td>
                            </tr>
                        )
                    })
                }</tbody>
            </table>
        )
    }
}
export default Cart;