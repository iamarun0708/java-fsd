import { Link } from 'react-router-dom';
import trainersMock from './TrainersMock';

function TrainersList() {
    return (
        <div>
            <h2>Trainers List</h2>
            <ul>
                {trainersMock.map(trainer => (
                    <li key={trainer.Trainerid}>
                        <Link to={`/trainers/${trainer.Trainerid}`}>{trainer.Name}</Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default TrainersList;
