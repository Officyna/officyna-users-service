db = db.getSiblingDB('officyna-users');

const adminExists = db.users.findOne({ email: 'admin@officyna.com' });

if (!adminExists) {
    db.users.insertOne({
        name: 'Administrador',
        email: 'admin@officyna.com',
        password: '$2a$10$fu3CVofY.g46Oop5BWExcuJmRxUfgRCPPIiSC0yj8j60cQSeapq0S',
        userRole: 'ADMIN',
        active: true,
        createdAt: new Date(),
        updatedAt: new Date(),
        _class: 'br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument'
    });
    print('Admin user created.');
} else {
    print('Admin user already exists, skipping.');
}

