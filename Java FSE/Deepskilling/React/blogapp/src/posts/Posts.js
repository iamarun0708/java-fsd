import React, { Component } from 'react';
import Post from '../Post'

class Posts extends Component {
    constructor(props) {
        super(props)
        this.state = {
            posts: []
        }
    }

    Loadpost = () => {
        fetch("https://jsonplaceholder.typicode.com/posts")
            .then(res => res.json())
            .then(res => {
                let pl = res.map(
                    item => new Post(item.id, item.title, item.body)
                )
                this.setState({
                    posts: pl
                })
            })
    }

    componentDidMount() {
        this.Loadpost()
    }
    render() {
        return (
            <div>{
                this.state.posts.map(post => (
                    <div key={post.id}>
                        <h1>{post.title}</h1>
                        <p>{post.body}</p>
                    </div>
                )
                )
            } </div>
        )
    }
    componentDidCatch(error, info) {
        alert(error)
    }
}



export default Posts;