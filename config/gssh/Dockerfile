FROM ubuntu:14.04

RUN apt-get -y update
RUN apt-get -y install openssh-server
RUN apt-get -y install git

# Setting openssh
RUN mkdir /var/run/sshd
RUN sed -i "s/#PasswordAuthentication yes/PasswordAuthentication no/" /etc/ssh/sshd_config

# Adding git user
RUN adduser --system git
RUN mkdir -p /home/git/.ssh

# Add a key
RUN echo '' > /home/git/.ssh/authorized_keys
RUN echo 'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDOP5VvWkNV90iY5XVVyOkDKB+87mRixvSOvokXgmLcmyUS/XObdfxBwMU/5COwWRK8HoPkIjUCkgGi4zkrgzWXt4cD3OiiPy+UmbT2PP3XaXhgpmqOaBDRzIZg88GbTg2cUEkeSB5G37dy8AzwFQ6VtPnBX2GTZJf3xcNX5OHQWJL9CoBLJ5J6o4ITyMutxurXZpCsw8C4EM+zZEKBevEMRNi2eragzXu96mqQANVhoxVxipygXka4Sxn8BiGVnbeIEBzQgufhhJaFz7p0P1TOsPZ73AMis9/ieK1+3sTyiirqiv5Ua4ZVZSAgqJic9SzLYof39XZ+7sxC5czc90b7' >> /home/git/.ssh/authorized_keys

# User settings
RUN sed -i s#/home/git:/bin/false#/home/git:/bin/bash# /etc/passwd

# Add a repo
RUN mkdir -p /opt/git/guitar2.git
WORKDIR /opt/git/guitar2.git
RUN git init

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]
