INSERT INTO agencies (name)
VALUES
    ('Department of Defense');

INSERT INTO vendors (name)
VALUES
    ('HUMANA GOVERNMENT BUSINESS INC'),
    ('KRATOS DEFENSE & SECURITY SOLUTIONS INC'),
    ('RAYTHEON COMPANY'),
    ('SCIENCE APPLICATIONS INTERNATIONAL CORPORATION');

INSERT INTO contracts (
    award_id,
    vendor_id,
    agency_id,
    award_amount,
    start_date,
    end_date,
    description
)
VALUES
    (
        'HT940216C0001',
        (SELECT id FROM vendors WHERE name = 'HUMANA GOVERNMENT BUSINESS INC'),
        (SELECT id FROM agencies WHERE name = 'Department of Defense'),
        51269205263.03,
        '2016-08-01',
        '2025-12-31',
        'Managed care support contract for TRICARE services.'
    ),
    (
        'N0001923C0021',
        (SELECT id FROM vendors WHERE name = 'KRATOS DEFENSE & SECURITY SOLUTIONS INC'),
        (SELECT id FROM agencies WHERE name = 'Department of Defense'),
        238798157.30,
        '2023-01-01',
        '2028-12-31',
        'Defense systems and services contract.'
    ),
    (
        'W31P4Q19C0018',
        (SELECT id FROM vendors WHERE name = 'RAYTHEON COMPANY'),
        (SELECT id FROM agencies WHERE name = 'Department of Defense'),
        225397657.37,
        '2019-01-01',
        '2024-12-31',
        'Missile systems support contract.'
    ),
    (
        'FA867818C0002',
        (SELECT id FROM vendors WHERE name = 'KRATOS DEFENSE & SECURITY SOLUTIONS INC'),
        (SELECT id FROM agencies WHERE name = 'Department of Defense'),
        119284283.48,
        '2018-01-01',
        '2024-12-31',
        'Aircraft and weapons systems support.'
    ),
    (
        'FA873021F0050',
        (SELECT id FROM vendors WHERE name = 'SCIENCE APPLICATIONS INTERNATIONAL CORPORATION'),
        (SELECT id FROM agencies WHERE name = 'Department of Defense'),
        119243788.25,
        '2021-01-01',
        '2026-12-31',
        'Information technology and mission support services.'
    );