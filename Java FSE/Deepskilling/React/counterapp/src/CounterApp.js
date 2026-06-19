import React, { Component } from 'react';
import './style.css'
class CounterApp extends Component {
    constructor(props) {
        super(props)
        this.state = {
            entrycount: 0,
            exitcount: 0,
            c: 0
        }
    }
    updateEntry() {
        this.setState((prevState, props) => {
            return { entrycount: prevState.entrycount + 1 }
        })
    }
    updateExit() {
        this.setState((prevState, props) => {
            return { exitcount: prevState.exitcount + 1 }
        })
    }
    render() {
        return (
            <div className='container'><div>
                <button onClick={this.updateEntry.bind(this)}>Login</button>
                <span> {this.state.entrycount} People Entered</span>
            </div>
                <div>
                    <button onClick={this.updateExit.bind(this)}>Logout</button>
                    <span>{this.state.exitcount} People Exited</span>
                </div>
            </div>
        )
    }
}
export default CounterApp