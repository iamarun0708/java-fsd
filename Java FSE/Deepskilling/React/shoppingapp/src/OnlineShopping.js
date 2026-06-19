import React, { Component } from 'react';
import Cart from './Cart'
class OnlineShopping extends Component {
    render() {
        const cartInfo = [{ itemname: "Laptop", price: 80000 },
        { itemname: "TV", price: 120000 },
        { itemname: "Washing Machine", price: 50000 },
        { itemname: "Mobile", price: 30000 },
        { itemname: "Fridge", price: 70000 }
        ]
        return (
            <div>
                <h1>Item Ordered :</h1>
                <Cart item={cartInfo} />
            </div>
        )
    }
}
export default OnlineShopping