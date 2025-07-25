import psycopg2

import os
from court_scraper.db.models import CourtCase
from dotenv import load_dotenv, find_dotenv
import time
import logging
# load_dotenv(dotenv_path="../../.env.dev", override=True) # override means that it removes any lingering .env vars
load_dotenv(find_dotenv())

log = logging.getLogger(__name__)

def get_connection():

    log.debug(f"""  name = {os.getenv("DB_NAME")}\n
                    user = {os.getenv("DB_USER")}\n
                    host = {os.getenv("DB_HOST")}\n
                    port = {os.getenv("DB_PORT")}"""
    )


    return psycopg2.connect(
        dbname = os.getenv("DB_NAME"),
        user = os.getenv("DB_USER"),
        password = os.getenv("DB_PASS"),
        host = os.getenv("DB_HOST"),
        port = os.getenv("DB_PORT")
    )

    

def get_court_id_by_city(city):
    conn = get_connection()  
    
    try:
        with conn:
            with conn.cursor() as cur:
                cur.execute("""
                    SELECT id FROM court WHERE city = %s
                    """,
                    (city,)) #trailing comma is important as execute needs a tuple as arg, and that is how to designate
                row = (cur.fetchone())
                return row[0] if row else None
                
    except Exception as e:
        log.warning(f"issue with getting court by ID: {e}")
    finally:
         conn.close()
        
def insert_court_case(court_case:CourtCase, court_id):
    conn = get_connection()
    try:
        with conn:
            with conn.cursor() as cur:
                cur.execute(
                    '''
                        INSERT INTO court_case(
                        start_time_epoch,
                        created_at,
                        duration,
                        case_details,
                        case_id,
                        claimant,
                        defendant,
                        is_minor,
                        hearing_type,
                        hearing_channel,
                        court_id
                        )
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                        RETURNING id
                    ''',
                    (
                        court_case.start_time_epoch,
                        round(time.time()),
                        court_case.duration,
                        court_case.case_details,
                        court_case.case_id,
                        court_case.claimant,
                        court_case.defendant,
                        court_case.is_minor,
                        court_case.hearing_type,
                        court_case.hearing_channel,
                        court_id
                    )
                )
        
    # expected frequently as cases scraped daily, but often a case is up for multiple days
    except psycopg2.IntegrityError as e:
        log.debug(f"case already exists: {court_case.case_id}\n")
    # likely to indicate an issue with parsing the rows of court case into the correct columns. Possible also just anomolously long values.
    except psycopg2.errors.StringDataRightTruncation as e:
        log.warning(f"value length exceeds column capacity: {court_case.__dict__}")
    finally:
        conn.close()
