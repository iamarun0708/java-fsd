import React, { Component } from 'react';
import './App.css';
import ListofPlayer from './ListofPlayer';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      players: [
        { id: 1, name: 'Virat Kohli', country: 'India', role: 'Batsman', matches: 274, runs: 12898, wickets: 4 },
        { id: 2, name: 'Rohit Sharma', country: 'India', role: 'Batsman', matches: 243, runs: 9987, wickets: 8 },
        { id: 3, name: 'MS Dhoni', country: 'India', role: 'Wicket-Keeper', matches: 350, runs: 10773, wickets: 0 },
        { id: 4, name: 'Jasprit Bumrah', country: 'India', role: 'Bowler', matches: 72, runs: 67, wickets: 121 },
        { id: 5, name: 'Ravindra Jadeja', country: 'India', role: 'All-Rounder', matches: 168, runs: 2531, wickets: 220 },
        { id: 6, name: 'KL Rahul', country: 'India', role: 'Batsman', matches: 66, runs: 2088, wickets: 0 },
        { id: 7, name: 'Shubman Gill', country: 'India', role: 'Batsman', matches: 38, runs: 1611, wickets: 0 },
      ]
    };
  }

  render() {
    const { players } = this.state;
    return (
      <div className="App">
        <h1>🏏 Cricket Player Stats</h1>
        <table border="1" cellPadding="10" cellSpacing="0" style={{ margin: '20px auto', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ backgroundColor: '#1a237e', color: 'white' }}>
              <th>ID</th>
              <th>Name</th>
              <th>Country</th>
              <th>Role</th>
              <th>Matches</th>
              <th>Runs</th>
              <th>Wickets</th>
            </tr>
          </thead>
          <tbody>
            {players.map(player => (
              <ListofPlayer key={player.id} player={player} />
            ))}
          </tbody>
        </table>
      </div>
    );
  }
}

export default App;
