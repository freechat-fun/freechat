import argparse
import json
import sys

import requests
import urllib3

from jsonpath_ng import jsonpath, parse


DEFAULT_INPUT = 'http://127.0.0.1:8080/public/openapi/v3/api-docs/g-all'
DEFAULT_OUTPUT = 'openapi.properties'


urllib3.disable_warnings()


def _fetch_url(url: str) -> object:
    headers = {
        'Accept': 'application/json',
    }
    resp = requests.get(url=url, headers=headers, verify=False)
    resp.raise_for_status()
    if resp.content:
        resp.encoding = 'utf-8'
        return resp.json()


def _fetch_file(path: str) -> object:
    with open(path, 'r') as f:
        return json.loads(f.read())


def _handle_newline(content: str) -> str:
    return content.translate(str.maketrans({'\\': r'\\', '\n': r'\n'}))


def _load(infile: str) -> object:
    if infile.startswith('https://') or infile.startswith('http://'):
        return _fetch_url(infile)
    else:
        return _fetch_file(infile)


def _save(outfile, items: list):
    if isinstance(outfile, str):
        with open(outfile, 'w') as f:
            f.writelines([f'{item.full_path}={_handle_newline(item.value)}\n'
                          for item in filter(lambda i: isinstance(i.value, str) and i.value != 'OK', items)])
    else:
        outfile.writelines([f'{item.full_path}={_handle_newline(item.value)}\n'
                            for item in filter(lambda i: isinstance(i.value, str) and i.value != 'OK', items)])


def json_to_prop(infile: str, outfile: str) -> str:
    spec = _load(infile)
    p = parse('$..[title,summary,description]')
    items = p.find(spec)
    if not items:
        print(sys.stderr, f'Oops...no content was found in {infile}')
        return
    _save(outfile if outfile else sys.stdout, items)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-i', '--input', default=DEFAULT_INPUT)
    parser.add_argument('-o', '--output', default=DEFAULT_OUTPUT)

    args = parser.parse_args()
    json_to_prop(args.input, args.output)
