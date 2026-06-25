import React from 'react';


function ListofPlayer() {
    const ListofPlayers = [
        { name: "Virat Kohli", score: 85 },
        { name: "Rohit Sharma", score: 92 },
        { name: "Shubman Gill", score: 75 },
        { name: "KL Rahul", score: 65 },
        { name: "Hardik Pandya", score: 55 },
        { name: "Ravindra Jadeja", score: 80 },
        { name: "R Ashwin", score: 60 },
        { name: "Mohammed Shami", score: 45 },
        { name: "Jasprit Bumrah", score: 70 },
        { name: "Ishan Kishan", score: 68 },
        { name: "Surya Kumar Yadav", score: 95 }
    ]
    const LowScore = ListofPlayers.filter(player => player.score < 70)
    return (
        <div>
            <h1>List of Player</h1>
            <h3>All Player</h3>
            <ul></ul>
        </div>
    );
}
export default ListofPlayer;                