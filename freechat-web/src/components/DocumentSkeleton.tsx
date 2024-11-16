import { forwardRef } from 'react';
import { Box, BoxProps, Card, Skeleton, Stack } from '@mui/joy';

type DocumentSkeletonProps = BoxProps & {
  lines?: number;
  loading?: boolean;
};

const DocumentSkeleton = forwardRef<HTMLDivElement, DocumentSkeletonProps>(
  (props, ref) => {
    const { lines = 1, loading = true, sx, ...others } = props;

    const titles = Array.from({ length: lines }, (_, index) => (
      <Card key={`title-${index}`} variant="plain">
        <Skeleton />
      </Card>
    ));

    return (
      <Box
        ref={ref}
        sx={{
          display: loading ? 'grid' : 'none',
          gridTemplateColumns: '1fr 3fr',
          alignItems: 'stretch',
          gap: 2,
          pb: 4,
          pr: 4,
          ...sx,
        }}
        {...others}
      >
        <Stack spacing={1}>{titles}</Stack>
        <Card variant="plain">
          <Skeleton />
        </Card>
      </Box>
    );
  }
);

export default DocumentSkeleton;
