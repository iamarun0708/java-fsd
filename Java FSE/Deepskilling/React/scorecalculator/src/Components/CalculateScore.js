import './Styles/mystyle.css'

const percentToDecimal = (dec) => {
    return (dec.toFixed(2) + "%")
}
const calScore = (total, goal) => {
    return percentToDecimal(total / goal)
}
export const CalculateScore = ({ name, school, total, goal }) => {
    return (
        <div className="formatstyle">
            <h1>
                <font color="Brown">Student Details : </font>
            </h1>
            <div className='Name'>
                <b><span>Name : </span></b>
                <span>{name}</span>
            </div>
            <div className='School'>
                <b><span>School : </span></b>
                <span>{school}</span>
            </div>
            <div className='Total'>
                <b><span>Total : </span></b>
                <span>{total} Marks</span>
            </div>
            <div className='Score'>
                <b><span>Score : </span></b>
                <span>{calScore(total, goal)}</span>
            </div>


        </div>
    )
}