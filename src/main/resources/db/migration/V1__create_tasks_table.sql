create TABLE IF NOT EXISTS Tasks (
    id serial  primary key ,
    task_type text,
    summary text,
    description text,
    status text
)