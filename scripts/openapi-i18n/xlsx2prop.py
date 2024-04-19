import argparse
import os

import openpyxl

DEFAULT_INPUT = 'openapi.xlsx'
DEFAULT_OUTPUT = 'openapi.properties'


def _handle_newline(content: str) -> str:
    return content if content.endswith('\n') else content + '\n'


def _from_xlsx(infile: str) -> list:
    props = []
    file = openpyxl.load_workbook(infile)
    sheet = file.active
    for row in sheet.rows:
        items = []
        key = None
        for cell in row:
            if not key:
                key = cell.value
                continue
            item = (key, cell.value)
            items.append(item)
        props.append(items)
    return zip(*props)


def xlsx_to_prop(infile: str, outfile: str):
    props = _from_xlsx(infile)
    basename, ext = os.path.splitext(outfile)
    i = 1
    for prop in props:
        outfile_name = f'{basename}_{str(i)}{ext}'
        i += 1
        with open(outfile_name, 'w') as f:
            f.writelines([f'{k}={_handle_newline(v)}' for k, v in filter(lambda item: item[1] and item[1].strip(), prop)])


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-i', '--input', default=DEFAULT_INPUT)
    parser.add_argument('-o', '--output', default=DEFAULT_OUTPUT)

    args = parser.parse_args()
    xlsx_to_prop(args.input, args.output)
