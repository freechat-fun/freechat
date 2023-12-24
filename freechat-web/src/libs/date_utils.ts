export function formatDate(date: Date | undefined): string {
  if (!date) {
    return '';
  }
  
  const pad = (n: number) => n < 10 ? '0' + n : n;

  const year = date?.getFullYear() || 9999;
  const month = pad((date?.getMonth() || 11) + 1);
  const day = pad(date?.getDate() || 31);
  const hours = pad(date?.getHours() || 23);
  const minutes = pad(date?.getMinutes() || 59);
  const seconds = pad(date?.getSeconds() || 59);

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

export function getDateLabel(date: Date, locale: string, showTime: boolean = false): string {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    ...(showTime && {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    }),
  };
  return new Intl.DateTimeFormat(locale, options).format(date);
}

export function getSecondsBetweenDates(earlier: Date | undefined | null, later: Date | undefined | null) {
  const timestamp1 = earlier?.getTime() || 0;
  const timestamp2 = later?.getTime() || 0;
  const millisecondsDifference = timestamp2 - timestamp1;
  return Math.floor(millisecondsDifference / 1000);
}