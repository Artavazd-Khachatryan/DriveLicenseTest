#!/usr/bin/env python3
"""Stamp a content version into the bundled questions database.

The app compares this stamp against Database.CONTENT_VERSION at startup and
refreshes the question tables on users' devices when the bundled content is
newer. Run this after ANY change to question content, then bump
CONTENT_VERSION in database/.../Database.kt to the same number.

Usage:
    python3 set_content_version.py <version>
    python3 set_content_version.py --show
"""

import sqlite3
import sys
from pathlib import Path

DB_PATH = (
    Path(__file__).resolve().parent.parent
    / "database/src/commonMain/resources/license_test_questions.db"
)


def main() -> int:
    if len(sys.argv) != 2:
        print(__doc__)
        return 2

    conn = sqlite3.connect(DB_PATH)
    conn.execute(
        "CREATE TABLE IF NOT EXISTS Metadata (key TEXT NOT NULL PRIMARY KEY, value TEXT NOT NULL)"
    )

    if sys.argv[1] == "--show":
        row = conn.execute(
            "SELECT value FROM Metadata WHERE key = 'content_version'"
        ).fetchone()
        print(row[0] if row else "unset")
        return 0

    version = int(sys.argv[1])
    current = conn.execute(
        "SELECT value FROM Metadata WHERE key = 'content_version'"
    ).fetchone()
    if current and int(current[0]) >= version:
        print(f"error: current version is {current[0]}; new version must be higher")
        return 1

    conn.execute(
        "INSERT OR REPLACE INTO Metadata (key, value) VALUES ('content_version', ?)",
        (str(version),),
    )
    conn.commit()
    conn.execute("VACUUM")
    conn.close()
    print(f"stamped content_version={version} into {DB_PATH.name}")
    print("Remember to set Database.CONTENT_VERSION to the same value.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
